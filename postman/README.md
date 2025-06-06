# Gatling Postman - JavaScript and TypeScript demo projects

A simple showcase of JavaScript and TypeScript NPM projects using Gatling Postman.
Please also check out the [Gatling Postman reference documentation](https://docs.gatling.io/reference/script/protocols/postman/).

## Prerequisites

You need [Node.js](https://nodejs.org/en/download) v18 or later (LTS versions only) and npm v8 or later (included with Node.js).

## Use demo project

Run the typeScript sample:

```shell
cd typescript
npm install
npx gatling run --typescript --simulation basicSimulation # automatically download Gatling runtime, build the project, and run the basic ecomm simulation
```

Or the JavaScript sample:

```shell
cd javascript
npm install
npx gatling run --simulation basicSimulation # automatically download Gatling runtime, build the project, and run the basic ecomm simulation
```

The `gatling` command-line tool has a built-in help function:

```shell
npx gatling --help # List all available commands
npx gatling run --help # List options for the "run" command (--help also works for all other available commands)
```

## Included helper scripts

Note that both sample projects include a few aliases in the `package.json`'s `scripts` section, which you can use for convenience or refer to as examples:

```shell
npm run clean # Delete Gatling bundled code and generated reports
npm run format # Format code with prettier
npm run check # TypeScript project only, type check but don't build or run
npm run build # Build project but don't run
npm run basicSimulation # Run the included basic simulation
npm run advancedSimulation # Run the included advanced simulation
```
