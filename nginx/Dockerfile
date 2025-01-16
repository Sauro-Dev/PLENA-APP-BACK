FROM nginx:1.25.4-alpine

# Crear directorio para archivos estáticos
RUN mkdir -p /usr/share/nginx/html/static && \
    chown -R nginx:nginx /usr/share/nginx/html/static

# Copiar la configuración
COPY conf/nginx.conf /etc/nginx/nginx.conf

EXPOSE 80