FROM nginx:alpine

#COPY nginx.crt /etc/nginx/ssl/nginx.crt
#COPY nginx.key /etc/nginx/ssl/nginx.key

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY /dist /usr/share/nginx/html

EXPOSE 80
#EXPOSE 443

CMD ["nginx", "-g", "daemon off;"]