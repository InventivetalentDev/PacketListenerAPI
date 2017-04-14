#!/bin/bash
echo -e "Uploading Javadoc...\n"

cd $HOME
git config --global user.email "git-bot@inventivetalent.org"
git config --global user.name "InventiveBot"
git clone --quiet --branch=gh-pages https://${GITHUB_TOKEN}@github.com/InventivetalentDev/PacketListenerAPI gh-pages > /dev/null

cd gh-pages
git rm -rf .
cp -Rf $TRAVIS_BUILD_DIR/target/site/apidocs .
git add -f .
git commit -m "Javadoc for #$TRAVIS_BUILD_NUMBER"
git push -fq origin gh-pages > /dev/null

echo -e "Uploaded Javadoc\n"
