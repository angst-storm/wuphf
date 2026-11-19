IMAGE ?= wuphf-ums:latest
CLUSTER ?= wuphf

.PHONY: kind-c;uster delete-kind-cluster image postgres-db ums

kind-cluster:
	kind create cluster --name $(CLUSTER) --config kind-config.yaml
	kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
	kubectl wait --namespace ingress-nginx \
		--for=condition=ready pod \
		--selector=app.kubernetes.io/component=controller \
		--timeout=90s

delete-kind-cluster:
	kind delete cluster -n wuphf

postgres-db:
	kubectl create namespace postgres
	kubectl apply -k ../db/init
	helm install postgres oci://registry-1.docker.io/bitnamicharts/postgresql \
		--namespace postgres --values postgres/values.yaml

image:
	docker build -f docker/Dockerfile -t $(IMAGE) ..
	kind load docker-image $(IMAGE) --name $(CLUSTER)

ums:
	kubectl apply -k .
