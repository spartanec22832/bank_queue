# syntax=docker/dockerfile:1
FROM node:18-alpine AS build
WORKDIR /app

# Эти две переменные гарантируют,
# что npm ci не будет опускать devDependencies
ENV NODE_ENV=production
ENV NPM_CONFIG_PRODUCTION=false

COPY package.json package-lock.json ./
RUN npm ci

COPY . .

ARG VITE_API_URL
ENV VITE_API_URL=$VITE_API_URL

RUN node node_modules/vite/bin/vite.js build

FROM nginx:stable-alpine
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]