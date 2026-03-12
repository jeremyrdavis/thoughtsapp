# Build the React app
cd /Users/jeremyrdavis/Workspace/DevHub/thoughts-admin-frontend
VITE_API_BASE_URL=/api npm run build

## Copy the build output into Quarkus static resources
### Vite outputs to dist/ by default
cp -r dist/* /Users/jeremyrdavis/Workspace/DevHub/thoughts-admin/src/main/resources/META-INF/resources/

