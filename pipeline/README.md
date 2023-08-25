This pipelines folder is used to deploy this service into <env> To deploy

cd <PROJECT-ROOT> ./ops/tag.sh <env>

this will directly deploy into <env> environment

Example : to deploy into dev ./ops/tag.sh <env>

To Destroy, 1. set a env variable "_DESTROY=true" at Setup Trigger manually using GCP Dashboard 2. And trigger that "Setup Trigger" it manually 3. it will destroy the GCP resources and remove "_DESTROY=true" env variable from that Setup Trigger 4. to deploy again, just do "./ops/tag.sh setup-<env>" from repository OR triger manually using GCP Dashboard