import { PlayerSdk } from "@api.video/player-sdk";

/**
 * Functions for orchestrating iframes containing api.video videos
 */
export namespace Video {
    export type PlayerAddedEvent = Event & {
        detail: HTMLIFrameElement;
    }

    const colorDestroyed: string = "\x1b[31m";
    const colorNew:       string = "\x1b[32m";
    const colorPaused:    string = "\x1b[33m";
    const colorPlaying:   string = "\x1b[35m";

    const registeredPlayers = new Map<string, PlayerSdk>();

    export function pausePlayer(playerId: string): void {
        const player: PlayerSdk = forId(playerId);
        player.getPlaying(isPlaying => {
            if (isPlaying) {
                console.debug(`Pausing video player ${colorPaused}${playerId}`);
                player.pause();
            }
        });
    }

    export function pauseOtherPlayers(activePlayerId: string): void {
        console.debug(`Active video player is now ${colorPlaying}${activePlayerId}`);
        const activePlayer: PlayerSdk = forId(activePlayerId);
        registeredPlayers.forEach((player: PlayerSdk, playerId: string) => {
            if (player !== activePlayer) {
                pausePlayer(playerId);
            }
        });
    }

    export function initPlayer(wrapperId: string, videoId: string, token: string): void {
        const wrapper: HTMLElement = document.getElementById(wrapperId)!;
        const playerId: string = `#${wrapperId}-player`;

        const player = new PlayerSdk(`#${wrapperId}`, {
            id: videoId,
            token: token
        });

        player.addEventListener("play", () => pauseOtherPlayers(playerId));

        const iframe: HTMLIFrameElement = wrapper.querySelector("iframe:first-of-type")!;
        iframe.id = playerId;

        registeredPlayers.set(playerId, player);

        console.debug(`Video player initialized: ${colorNew}${playerId}`);

        document.dispatchEvent(new CustomEvent<HTMLIFrameElement>("videoPlayerAdded", {detail: iframe}));
    }

    export function destroyAllPlayers(): void {
        registeredPlayers.forEach((player: PlayerSdk, playerId: string) => {
            player.pause();
            player.destroy();
            console.debug(`Video player destroyed: ${colorDestroyed}${playerId}`);
        });
        registeredPlayers.clear();
    }

    export async function logRegisteredPlayers(): Promise<void> {
        const playerIds: Array<string> = [];
        for (let [playerId, player] of registeredPlayers) {
            await player.getPlaying() ?
                playerIds.push(`${colorPlaying}${playerId} (playing)`) :
                playerIds.push(`${colorPaused}${playerId}`);
        }
        console.info(`Registered video players:\n\t${playerIds.join("\n\t")}`);
    }

    function forId(playerId: string): PlayerSdk {
        return registeredPlayers.get(playerId)!;
    }
};