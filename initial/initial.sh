#!/bin/bash -e

#./ops/cloudbuild/initial/initial.sh <project> <repo> <servicename> <env>
#for example
#./ops/cloudbuild/initial/initial.sh sok-dev-svc infra-func infra-func dev

#sed -e 's/env/dev/g' -e 's/svc/demo-service/g' -e 's/projectname/sok-dev-svc/g' -e 's/repo/ops-sb-api/g' initial.tmpl > initial.yml
DIR=$( cd "$( dirname "$0" )" && pwd )
tmpl=$DIR/../../../tmp/temp.yaml
sed -e "s/svc/$3/g" -e "s/projectname/$1/g" -e "s/reponame/$2/g" -e "s/env/$4/g" $DIR/initial.tmpl > $tmpl
_NAME=setup-$4--$3
echo ${_NAME}

exist=$(gcloud beta builds triggers list --format="get(name)" --filter="name <= ${_NAME} AND name >= ${_NAME}" --project ${1} | wc -l);
echo $exist;

if [[ $exist -eq 0 ]]; 
then 
	echo "Not Exist, So Creating...."
    gcloud beta builds triggers import --source=$tmpl --project=$1;
else
	echo "Already Exist, So Ignoring...."
	if [ -n "$5" ] && [ "$5" == "_destroy" ];
	then
		echo "Destroying......."
		dest=$DIR/../../../tmp/dest.yaml
		echo gcloud beta builds triggers export ${_NAME} --destination $dest --project=$1;
		gcloud beta builds triggers export ${_NAME} --destination $dest --project=$1;
        sed -i '/  _ENV: /a \ \ _DESTROY: "true"' $dest
        echo gcloud beta builds triggers import --source=$dest --project=$1;
        gcloud beta builds triggers import --source=$dest --project=$1;
        echo ./ops/tag.sh setup-$4
	fi
fi