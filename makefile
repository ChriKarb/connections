pave_env:
	brew install docker
	brew install minikube
	brew install openjdk
build_docker:
	source current_builder.sh; docker build -t 2e2connections:latest .

minikube_start:
	minikube status || (minikube start; exit 1)
minikube_stop:
	minikube stop
build_minikube_docker_image_mvn:
    #https://medium.com/swlh/build-a-docker-image-using-maven-and-spring-boot-58147045a400
    #use defined dockerd daemon to use the minkube registry
	source current_builder.sh ; ./mvnw -X spring-boot:build-image -Dspring-boot.build-image.imageName=myrepo/myimage
build_local:
	./mvnw package

run_local: build_local
	java -j

show_local_docker_images:
	docker images

show_minikube_docker_images:
	source current_builder.sh ; docker images

set_minikube_docker:
	minikube -p minikube docker-env > current_builder.sh

test_local_run:
	curl http://localhost:8080/actuator/healthz

build_all: minikube_start set_minikube_docker build_image_mvn