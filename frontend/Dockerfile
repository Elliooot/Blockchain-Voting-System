# Multi-stage Build

# --- Build Stage ---
# Use the latest LTS version of Node.js
FROM node:20-alpine AS build
# Set the working directory inside the container
WORKDIR /app
# Copy package.json and package-lock.json
COPY package*.json ./
# Clean install dependencies according to package-lock.json
RUN npm ci
# Copy the rest of application files
COPY . .
# Accept build argument for API URL
ARG VITE_API_BASE_URL
# Set environment variables
ENV CI=false
ENV NODE_ENV=production
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
# Compile source code, bundle and compress
RUN npm run build

# --- Final Stage ---
FROM nginx:1.27-alpine
# Only copy from /app/dist to nginx server
COPY --from=build /app/dist /usr/share/nginx/html
# Copy custom nginx config file to handle frontend request correctly
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]