FROM nginx:alpine

# URL del backend para el proxy de /api/ (ver nginx.conf). Se puede
# sobreescribir en tiempo de ejecucion, por ejemplo vía
# docker-compose.yml. NGINX_ENVSUBST_FILTER limita la sustitucion de
# variables del template solo a BACKEND_URL, para no romper las variables
# nativas de nginx ($host, $remote_addr, etc.) que usa nginx.conf.
ENV BACKEND_URL=http://backend:8080
ENV NGINX_ENVSUBST_FILTER=BACKEND_URL

# Como .template (no conf.d directo): la imagen oficial de nginx corre
# envsubst sobre todo lo que esté en /etc/nginx/templates/*.template al
# arrancar el contenedor, y deja el resultado en /etc/nginx/conf.d/.
COPY nginx.conf /etc/nginx/templates/default.conf.template
COPY . /usr/share/nginx/html

EXPOSE 80
