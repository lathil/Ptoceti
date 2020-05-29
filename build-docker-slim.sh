docker build -t ptoceti/ptoceti-slim:latest -f Dockerfile-slim --build-arg PTOCETI_ASSEMBLY_TGZ_URL=./com.ptoceti.osgi.assembly/target/com.ptoceti.osgi.assembly-1.2.1-SNAPSHOT-Assembly.tar.gz .
