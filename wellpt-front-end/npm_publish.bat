echo "Before you publish,you have to process two steps, as follow:"
echo "step1: set your private repository,run in command----> npm config set registry http://ip:port"
echo "step1: login to your private repository, run in command -> npm login"
pause
cd wellapp-framework & npm unpublish --force & npm publish & cd .. & cd wellapp-admin & npm unpublish --force & npm publish & cd .. & cd wellapp-workflow & npm unpublish --force & npm publish & cd .. & cd wellapp-dyform & npm unpublish --force & npm publish & cd .. & cd wellapp-for-prod & npm unpublish --force & npm publish & cd .. & cd wellapp-mobile & npm unpublish --force & npm publish & cd .. & cd wellapp-page-assembly & npm unpublish --force & npm publish & cd .. & cd wellapp-web & npm unpublish --force & npm publish cd wellapp-theme & npm unpublish --force & npm publish

pause

