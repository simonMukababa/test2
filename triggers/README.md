This triggers folder is used to setup cloudbuild triggers, So , if you make any change into this folder then, at first, you have to apply this "triggers setup changes into cloudbuild"
To make the service-names , region, etc update <CURRENT-DIR>/metadata.yaml

To apply setup changes into CloudBuild follw this command cd <PROJECT-ROOT> ./ops/tag.sh setup-<env>

Example : to update triggers at dev envs ./ops/tag.sh setup-dev NOTE: at first time it will take some time to make url alive since ssl certificates probacation will take arround between 10min and 30min.

This will trigger another cloudbuild pipeline(not deployment) which is called as setup pipeline. since you are going to change build setup itself, So it will wait for your manager approval,
So, contact your manager to approve this cloud setup changes

after your manager approval , you can deploy the app by using cd <PROJECT-ROOT> ./ops/tag.sh <env>

this will directly deploy into <env> environment

Example : to deploy into dev ./ops/tag.sh <env>