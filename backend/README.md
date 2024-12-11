## Serverless Backend for WCA Statistics

This is currently a POC for a serverless backend. The current backend for statistics is a little bit slow, this is a POC to fix it in a serverless way.

## About

This is a [Nest](https://nestjs.com/) project built to run in AWS Lambda. You can code just like any nest project out there, but bear in mind that some adjustments may be necessary. You can [refer to this doc](https://docs.nestjs.com/faq/serverless), in case you need.

## Requirements

- [nvm](https://github.com/nvm-sh/nvm) or the correct [node version](.nvmrc).

## Installation

```bash
nvm use
npm install
```

## Running the app

```bash
npm run build
npx serverless offline
```

Visit [http://localhost:3000/dev/api](http://localhost:3000/dev/api).

## Test

```bash
npm run test:e2e
```

## Deploy

```bash
export STAGE=prd
npx serverless deploy --stage $STAGE
```
