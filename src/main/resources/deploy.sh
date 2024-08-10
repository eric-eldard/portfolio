ROOT=/opt/portfolio
USER=portfolio-app

echo

if [[ ! -d $ROOT ]]
then
  echo "Creating ${ROOT}..."
  sudo mkdir $ROOT
fi

echo "Moving ${ARTIFACT_FINAL_NAME}.war to ${ROOT}..."
sudo mv ~/${ARTIFACT_FINAL_NAME}.war $ROOT

echo "Moving run.sh ${ROOT}..."
sudo mv ~/run.sh $ROOT

echo "Setting run.sh as executable..."
sudo chmod u+x $ROOT/run.sh

if [[ ! -d $ROOT/logs ]]
then
    echo "Creating ${ROOT}/logs..."
  sudo mkdir $ROOT/logs
fi

echo "Changing ownership of ${ROOT} to user portfolio-app..."
sudo chown -R $USER $ROOT

echo "Portfolio app successfully deployed."
echo
echo "Kill existing process, then run run.sh from ${ROOT} as user ${USER}"