# jira-epic-fan-out
Utility script to create component issues for an epic in jira.

When called with an epic key it iterates over all components attached to the epic and creates one story per component linking it back to the given epic. It copies over some of the epic fields:
* description
* summary (prefixed with component title)
* reporter
* fixversions
* priority


The result is a number of new issues. 
It is also possible to configure a filter so that not all components are used for issue creation.

## Prerequesites
The script is using the [Ammonite Scala Scripting Engine](https://ammonite.io/#ScalaScripts) which makes it very easy to use.
The only prerequesites is an installed JDK (> 1.8) and the Ammonite Script runner bash script

Installation of ammonite is as simple as running:
````
 sudo sh -c '(echo "#!/usr/bin/env sh" && curl -L https://github.com/lihaoyi/Ammonite/releases/download/1.6.5/2.12-1.6.5) > /usr/local/bin/amm && chmod +x /usr/local/bin/amm' && amm
````

For the script itself, only this repository needs to be checked out. 

## Usage
To configure the script the following settings need to be changed in the Vars.sc:
* **domain** The domain of your jira cloud instance. E.g. "https://somecorp.atlassian.net"
* **user** The username you login with or a synthetic user
* **token** Your password or a dedicated API token. Atlassian lets you create these tokens [here](https://id.atlassian.com/manage/api-tokens).
* **componentFilter** In case you only want to act for certain components within the epic, put them in this list and only these once will be used. Note, that these are the component titles and not the ids (for better readability). E.g. List("componentA", "component B")

Afterwards, you can simply run the EpicFanout.sc with the epic key as parameter:

````
amm EpicFanout.sc XY-KEY
````

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
