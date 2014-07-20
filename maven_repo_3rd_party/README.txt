to install dependencies in this repository, use a command like the following:

mvn install:install-file -Dfile=~/Downloads/littlefluffytoys_r6.jar \
                         -DgroupId=com.littlefluffytoys \
                         -DartifactId=locationlibrary \
                         -Dversion=r6 \
                         -Dpackaging=jar \
                         -DgeneratePom=true \
                         -DcreateChecksum=true \
                         -DlocalRepositoryPath=maven_repo_3rd_party

