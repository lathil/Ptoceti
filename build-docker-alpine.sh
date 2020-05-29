docker build -t ptoceti/ptoceti-alpine:latest -f Dockerfile-alpine --build-arg PTOCETI_ASSEMBLY_TGZ_URL=./com.ptoceti.osgi.assembly/target/com.ptoceti.osgi.assembly-1.2.1-SNAPSHOT-Assembly.tar.gz .
