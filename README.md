# connections 

1. [What is it?](#what-is-it)
1. [Features](#features)
1. [Installation](#install)
1. [Usage](#usage)

## <a name="what-is-it"></a>What is it?

connections is a small experiment to check for reachable connections

The checks is implemented as a number of prometheus metrics based on the micrometer framework 


## <a name="features">Features

* spring boot, helm, maven image_build, micrometer prometheus metrics
* additional tools:
    * [helm](): we like to have one (just a simple wrapper `helm create <helmchart>`)
    * [minikube](): a local environment for deploying pods and container
    * [mvnw](): build with a wamevn wrapper (from https://start.spring.io/)
    * [makefile](https://github.com/aws/aws-cli): better than a readme ?
    * [brew](): used for installing packages
    
## <a name="install"></a>Installation

git clone https://github.com/ckone4You/connections.git

1. use makefile:
      1. run `make pave_env`
      1. run `make build_all `

### <a name="usage"></a>Usage

