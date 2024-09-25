import { PlayerSdk } from "@api.video/player-sdk";

/**
 * Functions for orchestrating iframes containing api.video videos
 */
export namespace Video {
    const colorDestroyed: string = "\x1b[31m";
    const colorNew:       string = "\x1b[32m";
    const colorPaused:    string = "\x1b[33m";
    const colorPlaying:   string = "\x1b[35m";

    const registeredPlayers = new Map<PlayerSdk, string>();

    export function pausePlayer(player: PlayerSdk): void {
        player.getPlaying(isPlaying => {
            if (isPlaying) {
                console.debug(`Pausing video player ${colorPaused}${id(player)}`);
                player.pause();
            }
        });
    }

    export function pauseOtherPlayers(activePlayer: PlayerSdk): void {
        console.debug(`Active video player is now ${colorPlaying}${id(activePlayer)}`);
        registeredPlayers.forEach((playerId: string, player: PlayerSdk) => {
            if (player !== activePlayer) {
                pausePlayer(player);
            }
        });
    }

    export function initPlayer(wrapperId: string, videoId: string, token: string): void {
        const player = new PlayerSdk(`#${wrapperId}`, {
            id: videoId,
            token: token
        });

        player.addEventListener("play", () => pauseOtherPlayers(player));

        const playerId: string = `#${wrapperId}-player`;
        document.querySelector(`#${wrapperId} > iframe`)!.id = playerId;
        registeredPlayers.set(player, playerId);

        console.debug(`Video player initialized: ${colorNew}${playerId}`);
    }

    export function destroyAllPlayers(): void {
        registeredPlayers.forEach((playerId: string, player: PlayerSdk) => {
            player.pause();
            player.destroy();
            console.debug(`Video player destroyed: ${colorDestroyed}${playerId}`);
        });
        registeredPlayers.clear();
    }

    export async function logRegisteredPlayers(): Promise<void> {
        const playerIds: Array<string> = [];
        for (let [player, playerId] of registeredPlayers) {
            await player.getPlaying() ?
                playerIds.push(`${colorPlaying}${playerId} (playing)`) :
                playerIds.push(`${colorPaused}${playerId}`);
        }
        console.info(`Registered video players:\n\t${playerIds.join("\n\t")}`);
    }

    function id(player: PlayerSdk): string {
        return registeredPlayers.get(player)!;
    }
};