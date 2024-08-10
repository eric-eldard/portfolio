fuser -k 8080/tcp

json=`aws secretsmanager get-secret-value --secret-id apiVideo --query SecretString --output text`
export API_VIDEO_KEY=`echo $json | jq -r ".apiKey"`

json=`aws secretsmanager get-secret-value --secret-id portfolio-app-rds-access --query SecretString --output text`
host=`echo $json | jq -r ".host"`
port=`echo $json | jq -r ".port"`
db=`echo $json | jq -r ".dbInstanceIdentifier"`
export SPRING_DATASOURCE_URL="jdbc:mysql://${host}:${port}/${db}"
export SPRING_DATASOURCE_USERNAME=`echo $json | jq -r ".username"`
export SPRING_DATASOURCE_PASSWORD=`echo $json | jq -r ".password"`

nohup java -jar ${ARTIFACT_FINAL_NAME}.war > logs/output.log 2>&1 &