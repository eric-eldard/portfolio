# Portfolio Count Lines Of Code

BOLD_WHITE="\e[1;97m"
RESET="\e[0m"

printf "\n\n${BOLD_WHITE}Lines of application code${RESET}\n\n"
cloc --not-match-d="(node_modules|out|generated)" \
 maintenance-page \
 scripts \
 sql \
 src/main \
 pom.xml

printf "\n\n${BOLD_WHITE}Lines of application test code${RESET}\n\n"
cloc --fullpath --not-match-d="resources/test/portfolio" src/test

printf "\n\n${BOLD_WHITE}Lines of portfolio asset code${RESET}\n\n"
cloc assets/portfolio
