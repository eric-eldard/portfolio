# Run `mvn generate-resources` from project root to execute this script. It will:
# 1. download all TypeScript dependencies
# 2. compile all TypeScript to JavaScript
# 3. minify JavaScript & generate source maps
# 4. copy output to webapp/public/assets/scripts/generated

rm -rf out

mkdir -p out/min

npm install

# https://www.npmjs.com/package/typescript
# https://www.npmjs.com/package/browserify
node_modules/.bin/tsc && node_modules/.bin/browserify out/bundler.js -o out/portfolio.bundle.js

# https://www.npmjs.com/package/uglify-js
node_modules/.bin/uglifyjs out/portfolio.bundle.js \
  --output out/min/portfolio.bundle.min.js \
  --source-map "base=out,url=portfolio.bundle.min.js.map" \
  --mangle \
  --compress

node_modules/.bin/uglifyjs out/utils.js \
  --output out/min/utils.min.js \
  --source-map "base=out,url=utils.min.js.map" \
  --mangle \
  --compress