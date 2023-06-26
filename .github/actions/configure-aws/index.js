const core = require('@actions/core')
const github = require('@actions/github')
const AWS = require('aws-sdk');

AWS.config.update({region: 'eu-west-2'})

try {
    var codeArtifact = new AWS.CodeArtifact({apiVersion: '2018-09-22'});
    codeArtifact.listPackages({
        domain: 'zacheryharley-java',
        repository: 'lambda-light',
        namespace: 'uk.co.zacheryharley',
    }, function (err, data) {
        console.log(err);

        data.packages.forEach(function (value) {
            codeArtifact.listPackageVersions({}, function (e, d) {
                d.versions.forEach(version => {
                    console.log(d.package + ":" + version.version + ":" + version.status);
                })
            })
        })
    });


    const name = core.getInput('name');
    const payload = JSON.stringify(github.context.payload, undefined, 2);
    console.log(`The event payload: ${payload}`);
} catch (error) {
    core.setFailed(err.message);
}