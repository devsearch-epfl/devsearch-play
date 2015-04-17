# DevSearch Play [![Build Status](https://travis-ci.org/devsearch-epfl/devsearch-play.svg?branch=master)](https://travis-ci.org/devsearch-epfl/devsearch-play)

This is the front-end web application for DevSearch.

## Dependency import

- The devsearch-ast has been imported by locally publishing it. Clone the devsearch-ast repo, cd into it and run sbt publishLocal

## Setup

- Make sure the environment variables are set. For example, edit the file `deploy/env.sh` and define all the variables and then load it with `source deploy/env.sh`.

- To setup your DSA key pair, follow a simple guide such as: http://www.cyberciti.biz/faq/ssh-password-less-login-with-dsa-publickey-authentication/
Don't forget to put your public key on the server, otherwise you won't be able to connect.

- We need the fingerprint of the server to be stored, so make sure you can SSH into the sever from the system your are running Play.
```
ssh ${BIGDATA_USER}@icdataportal2.epfl.ch
```
- For now, we will have a dummy script that will return the search results. Inside `icdataportal2.epfl.ch`, create the file `devsearch.sh` in your home directory, with the following contents:
```
echo devsearch-epfl/devsearch-play/app/controllers/Application.scala
echo devsearch-epfl/devsearch-play/app/search/SearchService.scala
```
- Add the following line to your `~/.ssh/known_hosts` file:
```
icdataportal2.epfl.ch,128.178.150.72 ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBBRa9987TbNjrY1BIUR+B3YTdvHM84z1qKVY0GMgqHvFNfXW+Puh51qve2sKBvZBnrxJYykcN7WKpbkGityZIF4=
```

## How to run

- Run `sbt ~run`
- Go to `http://localhost:9000`

## Azure setup

- Provision the machine
- Setup the machine:
```wget https://raw.githubusercontent.com/devsearch-epfl/devsearch-play/master/deploy/setup.sh && sh setup.sh```
- Define the environment variables in `deploy/env.sh`
- Setup SSH tunnel
- Compile and run the server:
```sh devsearch-play/deploy/start.sh```
