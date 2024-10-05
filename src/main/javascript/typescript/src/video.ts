import { PlayerSdk } from "@api.video/player-sdk";

/**
 * Functions for orchestrating iframes containing api.video videos
 */
export namespace Video {
    export type PlayerAddedEvent = Event & {
        detail: HTMLIFrameElement;
    }

    const colorDestroyed: string = "color: light-dark(#c00, #f00)";
    const colorNew:       string = "color: light-dark(#090, #4e4)";
    const colorPaused:    string = "color: light-dark(#b60, #e70)";
    const colorPlaying:   string = "color: light-dark(#b0b, #f0f)";

    const registeredPlayers = new Map<string, PlayerSdk>();

    export function pausePlayer(playerId: string): void {
        const player: PlayerSdk = forId(playerId);
        player.getPlaying(isPlaying => {
            if (isPlaying) {
                console.debug(`Pausing video player %c${playerId}`, `${colorPaused}`);
                player.pause();
            }
        });
    }

    export function pauseOtherPlayers(activePlayerId: string): void {
        console.debug(`Active video player is now %c${activePlayerId}`, `${colorPlaying}`);
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

        player.addEventListener("play", () => {
            wrapper.classList.add("playing");
            pauseOtherPlayers(playerId);
        });

        player.addEventListener("pause", () => wrapper.classList.remove("playing"));

        const iframe: HTMLIFrameElement = wrapper.querySelector("iframe:first-of-type")!;
        iframe.id = playerId;

        registeredPlayers.set(playerId, player);

        console.debug(`Video player initialized: %c${playerId}`, `${colorNew}`);

        document.dispatchEvent(new CustomEvent<HTMLIFrameElement>("videoPlayerAdded", {detail: iframe}));
    }

    export function destroyAllPlayers(): void {
        registeredPlayers.forEach((player: PlayerSdk, playerId: string) => {
            player.pause();
            player.destroy();
            console.debug(`Video player destroyed: %c${playerId}`, `${colorDestroyed}`);
        });
        registeredPlayers.clear();
    }

    export async function logRegisteredPlayers(): Promise<void> {
        const playerIds: Array<string> = [];
        const colors: Array<string> = [];
        for (let [playerId, player] of registeredPlayers) {
            const playing: boolean = await player.getPlaying();
            if (playing) {
                playerIds.push(`%c${playerId} (playing)`);
                colors.push(`${colorPlaying}`);
            }
            else {
                playerIds.push(`%c${playerId}`);
                colors.push(`${colorPaused}`);
            }
        }
        console.info(`Registered video players:\n\t${playerIds.join("\n\t")}`, ...colors);
    }

    function forId(playerId: string): PlayerSdk {
        return registeredPlayers.get(playerId)!;
    }
};