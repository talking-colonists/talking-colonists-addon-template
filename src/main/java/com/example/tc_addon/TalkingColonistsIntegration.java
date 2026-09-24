package com.example.tc_addon;

import com.google.gson.JsonObject;
import me.sshcrack.mc_talking.api.ApiFeature;
import me.sshcrack.mc_talking.api.TalkingColonistsApi;
import me.sshcrack.mc_talking.api.conversation.CitizenConversationService;
import me.sshcrack.mc_talking.api.conversation.ConversationLifecycleEvent;
import me.sshcrack.mc_talking.api.prompt.CitizenPromptService;
import me.sshcrack.mc_talking.api.prompt.PromptContribution;
import me.sshcrack.mc_talking.api.prompt.PromptTarget;
import me.sshcrack.mc_talking.api.registration.AddonRegistration;
import me.sshcrack.mc_talking.api.tool.AiQueryTool;
import me.sshcrack.mc_talking.api.tool.AiToolContext;
import me.sshcrack.mc_talking.api.tool.AiToolParameter;
import me.sshcrack.mc_talking.api.tool.AiToolRegistry;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Everything this addon adds to Talking Colonists. Each {@code register...} call returns an
 * {@link AddonRegistration}; closing it removes that hook again.
 *
 * <p>The mod metadata already requires Talking Colonists 2.1 or newer (see
 * {@code deps.talking_colonists_version}), so the 2.0 hooks used here are always present.
 * Optional features are still checked with {@link TalkingColonistsApi#supports(ApiFeature)}.
 */
public final class TalkingColonistsIntegration {
    /** Namespace for every id this addon registers; use your mod id. */
    private static final String NS = ExampleAddon.MOD_ID;
    private static final List<AddonRegistration> REGISTRATIONS = new ArrayList<>();

    private TalkingColonistsIntegration() {
    }

    public static void register() {
        if (!TalkingColonistsApi.isAvailable()) {
            ExampleAddon.LOGGER.warn("Talking Colonists API is not available; the addon stays inactive");
            return;
        }
        ExampleAddon.LOGGER.info("Talking Colonists API {}.{}", TalkingColonistsApi.runtimeApiMajorVersion(),
                TalkingColonistsApi.runtimeApiMinorVersion());

        REGISTRATIONS.add(registerPromptContributor());
        REGISTRATIONS.add(registerQueryTool());
        REGISTRATIONS.add(registerLifecycleListener());

        // An optional feature: only hook it up when the installed Talking Colonists supports it.
        if (TalkingColonistsApi.supports(ApiFeature.UTTERANCE_EVENTS)) {
            REGISTRATIONS.add(CitizenConversationService.registerUtteranceListener(NS + ":log_lines", 100,
                    event -> ExampleAddon.LOGGER.debug("{} said: {}", event.speakerName(), event.text())));
        }
    }

    /** Adds a line to the citizen's roleplay prompt. Keep contributions short: they cost tokens every turn. */
    private static AddonRegistration registerPromptContributor() {
        return CitizenPromptService.registerContributor(NS + ":weather", 100, context -> {
            if (context.target() != PromptTarget.CITIZEN_ROLEPLAY) return List.of();
            return List.of(PromptContribution.instruction(NS + ":weather", "Weather",
                    "If the player mentions the weather, you enjoy talking about it."));
        });
    }

    /**
     * A tool the model can call during a conversation to read game state. Query tools must not
     * change the world; use an {@code AiCommandTool} for that.
     */
    private static AddonRegistration registerQueryTool() {
        return AiToolRegistry.register(NS, "current_weather", new AiQueryTool() {
            @Override
            public String description() {
                return "Get the current weather where this citizen stands.";
            }

            @Override
            public AiToolParameter parameters() {
                return AiToolParameter.object(Map.of());
            }

            @Override
            public JsonObject executeQuery(AiToolContext context, JsonObject parameters) {
                Level level = context.citizen().level();
                JsonObject result = new JsonObject();
                result.addProperty("weather", level.isThundering() ? "thunderstorm" : level.isRaining() ? "rain" : "clear");
                result.addProperty("isDay", level.isDay());
                return result;
            }
        });
    }

    /** Called when any conversation with a citizen starts or ends. */
    private static AddonRegistration registerLifecycleListener() {
        return CitizenConversationService.registerLifecycleListener(NS + ":lifecycle", 100, event -> {
            String name = event.citizen().getName().getString();
            if (event.phase() == ConversationLifecycleEvent.Phase.STARTED) {
                ExampleAddon.LOGGER.info("{} conversation with {} started", event.kind(), name);
            } else {
                ExampleAddon.LOGGER.info("{} conversation with {} ended", event.kind(), name);
            }
        });
    }
}
