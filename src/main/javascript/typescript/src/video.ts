import PlayerSdk = require("./types/api.video");

/**
 * Functions for orchestrating iframes containing api.video videos
 */
namespace Video {
    const registeredPlayers: Array<PlayerSdk> = [];
    const colorDestroyed: string = "\x1b[31m";
    const colorNew:       string = "\x1b[32m";
    const colorPaused:    string = "\x1b[33m";
    const colorPlaying:   string = "\x1b[35m";

    function pausePlayer(player: PlayerSdk): void {
        player.getPlaying(isPlaying => {
            if (isPlaying) {
                console.debug(`Pausing video player ${colorPaused}${player.options.id}`);
                player.pause();
            }
        });
    }

    function pauseOtherPlayers(activePlayer: PlayerSdk): void {
        console.debug(`Active video player is now ${colorPlaying}${activePlayer.options.id}`);
        registeredPlayers.forEach(player => {
            if (player !== activePlayer) {
                pausePlayer(player);
            }
        });
    }

    export function initPlayer(wrapperId: string):void {
        const playerId: string = `${wrapperId}-player`;
        const playerElem = document.querySelector(`#${wrapperId} > iframe:first-of-type`);

        if (playerElem == null) {
            throw new Error(`Cannot find video player wrapper ID [${wrapperId}]`);
        }

        playerElem.id = playerId;

        const player = new PlayerSdk(`#${playerId}`, { id: playerId });
        player.addEventListener("play", () => pauseOtherPlayers(player));

        registeredPlayers.push(player);

        console.debug(`Video player initialized: ${colorNew}${playerId}`);
    }

    export function destroyAllPlayers(): void {
        registeredPlayers.forEach(player => {
            player.pause();
            player.destroy();
            console.debug(`Video player destroyed: ${colorDestroyed}${player.options.id}`);
        });
        registeredPlayers.length = 0;
    }

    export async function logRegisteredPlayers(): Promise<void> {
        const playerIds: Array<string> = [];
        for (let player of registeredPlayers) {
            const id = player.options.id;
            await player.getPlaying() ?
                playerIds.push(`${colorPlaying}${id} (Playing)`) :
                playerIds.push(`${colorPaused}${id}`);
        }
        console.info(`Registered video players:\n\t${playerIds.join("\n\t")}`);
    }
};