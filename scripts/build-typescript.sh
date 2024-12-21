mkdir -p out/min

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