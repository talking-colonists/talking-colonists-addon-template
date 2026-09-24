# Talking Colonists Addon Template

A minimal addon for [Talking Colonists](https://github.com/sshcrack/talking-colonists), the
MineColonies mod that lets you talk to citizens. Start your own addon from it.

It builds for **1.21.1 NeoForge** and **1.20.1 Forge** from one source tree, using
[Stonecutter](https://stonecutter.kikugie.dev/). It shows the four things most addons need:

| What | Where |
|---|---|
| Check which Talking Colonists API features are installed | `TalkingColonistsIntegration.register()` |
| Add a line to the citizen's prompt (a *prompt contributor*) | `registerPromptContributor()` |
| Give the model a tool it can call to read game state (a *query tool*) | `registerQueryTool()` |
| React when a conversation starts or ends (*lifecycle events*) | `registerLifecycleListener()` |

The full API reference is
[`docs/addon-api.md`](https://github.com/sshcrack/talking-colonists/blob/main/docs/addon-api.md).

## Make it yours

1. In `stonecutter.properties.toml`, change `mod.id`, `mod.name`, `mod.group`, `mod.authors`,
   the URLs and the license.
2. Rename the package `com.example.tc_addon`, and rename `src/main/resources/tc_addon_example.mixins.json`
   to `<your mod id>.mixins.json` (update the `package` inside it too).
3. Replace `src/main/resources/assets/icon.png`.
4. Replace the example weather prompt and tool with your own.

## Build and run

```sh
./gradlew buildAndCollect     # jars for both loaders in build/libs/
./gradlew runActiveClient     # client for the active version (see .sc_active_version)
./gradlew runActiveServer
```

Switch the active version with `./gradlew "Set active project to 1.20.1-forge"`, and run
`./gradlew "Reset active project"` before committing, so the sources stay in their 1.21.1 NeoForge
state. The pre-commit hook blocks commits that change `.sc_active_version`.

To talk to citizens in the dev client you need a Gemini API key in the Talking Colonists config.
Without one the mod still loads and your hooks are registered, but citizens stay silent.

## Depending on Talking Colonists

`gradle.properties` sets `deps.talking_colonists_version`. The addon **compiles against the API
artifact only** (`me.sshcrack:mc_talking-api`) and uses the full mod at runtime; the API is never
packed into your jar. Both come from `https://maven.sshcrack.me/releases`.

The same version is the minimum Talking Colonists version in the generated `mods.toml`, so the game
refuses to load the addon with an older Talking Colonists instead of crashing. Features newer than
that minimum are checked with `TalkingColonistsApi.supports(ApiFeature.X)` before use, as the
`UTTERANCE_EVENTS` listener shows.

To test against a Talking Colonists build that is not released yet, run
`./gradlew publishToMavenLocal` in the Talking Colonists repository. This template checks
`mavenLocal()` first.

## Rules worth knowing

- **Use your mod id as the namespace** of everything you register (`yourmod:thing`).
- **Keep every `AddonRegistration`** if you may want to remove a hook again; `close()` unregisters it.
- **Query tools must not change the world.** Use `AiCommandTool` for actions, and do world
  changes on the server thread (`context.supplyOnServerThread(...)`).
- **Prompt contributions cost tokens on every turn.** Keep them short and return nothing when
  they do not apply.
- **Mixins:** the template has none. If you add some, see the comment in `build.forge.gradle.kts`.

## License

The template is released into the public domain ([Unlicense](LICENSE)); license your addon however
you like.
