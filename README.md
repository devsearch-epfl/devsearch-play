# DevSearch Play [![Build Status](https://travis-ci.org/devsearch-epfl/devsearch-play.svg?branch=master)](https://travis-ci.org/devsearch-epfl/devsearch-play)

This is the front-end web application for DevSearch.

## Dependency import

- The devsearch-ast has been imported by locally publishing it. Clone the devsearch-ast repo, cd into it and run sbt publishLocal

## Setup credentials

- Make sure the environmental variables `BIGDATA_USER` and `BIGDATA_KEY` are set. For example, run the below before executing `sbt`:
```
export BIGDATA_USER="your_epfl_username"
export BIGDATA_KEY="path/to/your/dsa_private_key"

- To setup your dsa key pair, follow a simple guide such as: http://www.cyberciti.biz/faq/ssh-password-less-login-with-dsa-publickey-authentication/
Don't forget to put your public key on the server, otherwise you won't be able to connect.

```
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
