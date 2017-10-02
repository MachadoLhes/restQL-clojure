#!/usr/bin/env bash

set -e

# only do deployment, when travis detects a new tag
if [ ! -z "$TRAVIS_TAG" ]
then
    echo "on a tag -> set pom.xml <version> to $TRAVIS_TAG"
    mvn --settings .travis/settings.xml org.codehaus.mojo:versions-maven-plugin:2.3:set -DnewVersion=$TRAVIS_TAG 1>/dev/null 2>/dev/null

else
    echo "not on a tag -> keep snapshot version in pom.xml"

fi

if [ ! -z "$TRAVIS" -a -f "$HOME/.gnupg" ]; then
    shred -v ~/.gnupg/*
    rm -rf ~/.gnupg
fi

source .travis/gpg.sh

mvn clean compile deploy --settings .travis/settings.xml -DskipTests=true -B -U -V

if [ ! -z "$TRAVIS" ]; then
    shred -v ~/.gnupg/*
    rm -rf ~/.gnupg
fi