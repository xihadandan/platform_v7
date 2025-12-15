#!/bin/bash
echo "begin to publish...................."

for line in `ls  |grep -E "^wellapp.*" |grep -v "\.$" `
  do
   echo $line
    if [[ "$line" != "wellapp-web" ]] && [[ "$line" != "wellapp-init" ]]; then
     cd  ./${line}
     pwd
     npm unpublish --force
     npm publish
     cd ..
    fi
  done


echo "finish to publish...................."
