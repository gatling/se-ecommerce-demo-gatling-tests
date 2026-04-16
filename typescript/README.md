# Gatling JS - TypeScript ecommerce application load tests

A simple showcase of TypeScript NPM projects using Gatling JS. Please also check out the [introduction to JavaScript scripting](https://docs.gatling.io/tutorials/scripting-intro-js/) in the Gatling documentation.

## Prerequisites

You need [Node.js](https://nodejs.org/en/download) v20 or later (LTS versions only) and npm v10 or later (included with Node.js).

## Use ecommerce project

Run the TypeScript sample:

```shell
npm install
npx gatling run --simulation basic # automatically download Gatling runtime, build the project, and run the basic simulation
```

You can launch the [Gatling Recorder](https://docs.gatling.io/tutorials/recorder/) and use it to capture browser-based actions and help create a realistic user scenario:

```shell
npx gatling recorder
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
npm run format-check # Check code format with prettier
npm run check # TypeScript project only, type check but don't build or run
npm run build # Build project but don't run
npm run recorder # Starts the Gatling Recorder
npm run basic # Run the included basic simulation
npm run advanced # Run the included advanced simulation
```
