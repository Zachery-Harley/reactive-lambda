const core = require('@actions/core')
const github = require('@actions/github')
const {
    CodeartifactClient,
    DescribePackageVersionCommand,
    ListPackagesCommand,
    ListPackageVersionsCommand
} = require("@aws-sdk/client-codeartifact");


const runDate = new Date().getTime();
const unlistedTtl = 1;
const snapshotTtl = 10;

const domain = core.getInput("domain");
// const repository = core.getInput("repository");
const repository = "lambda-light"

const client = new CodeartifactClient({region: 'eu-west-2'});

class PackagePrune {

    async getAllPackagesWithVersions() {
        const packages = await this.getAllPackages();
        const versionToDelete = [];

        for (const packageInfo of packages) {
            const versions = await this.getAllPackageVersions(packageInfo);
            for (const version of versions) {

                if(this.shouldDelete(version)) {
                    console.log(`${version.packageName}:${version.version}:${version.status}`)
                }
            }
        }
    }

    shouldDelete(version) {
        if(version.status === "Unlisted") {
            return (runDate - new Date(version.publishedTime).getTime()) >= unlistedTtl;
        }

        if(version.version.includes('SNAPSHOT') > 0) {
            return (runDate - new Date(version.publishedTime).getTime()) >= snapshotTtl;
        }

        return false;
    }

    async getAllPackages() {
        let packagePage = undefined;
        const packages = [];

        do {
            packagePage = await this.getPackagePage(packagePage?.nextToken || undefined)

            for (const packageInfo of packagePage.packages || []) {
                packages.push(packageInfo);
            }
        } while (packagePage.nextToken !== undefined);

        return packages;
    }

    async getPackagePage(nextToken = undefined) {
        let listPackageCommand = new ListPackagesCommand({
            domain: domain,
            repository: repository,
            nextToken: nextToken
        });

        return await client.send(listPackageCommand);
    }

    async describePackageVersion(packageSummary, versionSummary) {
        const describePackageVersionCommand = new DescribePackageVersionCommand({
            domain: domain,
            repository: repository,
            package: packageSummary.package,
            namespace: packageSummary.namespace,
            format: packageSummary.format,
            packageVersion: versionSummary.version,
        })

        return await client.send(describePackageVersionCommand);
    }

    async getAllPackageVersions(packageSummary) {
        let versionPage = undefined;
        const versions = [];

        do {
            versionPage = await this.getPackageVersionPage(packageSummary, packageSummary?.nextToken || undefined);

            for (const versionInfo of versionPage.versions || []) {
                const result = await this.describePackageVersion(packageSummary, versionInfo);
                versions.push(result.packageVersion);
            }
        } while (versionPage.nextToken !== undefined);

        return versions;
    }

    async getPackageVersionPage(packageSummary, nextToken = undefined) {
        const listPackageVersionCommand = new ListPackageVersionsCommand({
            domain: domain,
            repository: repository,
            package: packageSummary.package,
            namespace: packageSummary.namespace,
            format: packageSummary.format,
            nextToken: nextToken,
        })
        return await client.send(listPackageVersionCommand);
    }
}

const output = async () => {
    await new PackagePrune().getAllPackagesWithVersions();
}

output();