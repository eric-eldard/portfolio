/**
 * Functions for orchestrating iframes containing api.video videos
 */
const video = (function() {

    const registeredPlayers = [];
    const colorDestroyed = "\x1b[31m";
    const colorNew       = "\x1b[32m";
    const colorPaused    = "\x1b[33m";
    const colorPlaying   = "\x1b[35m";

    function pausePlayer(player) {
        player.getPlaying(isPlaying => {
            if (isPlaying) {
                console.debug(`Pausing video player ${colorPaused}${player.options.id}`);
                player.pause();
            }
        });
    }

    function pauseOtherPlayers(activePlayer) {
        console.debug(`Active video player is now ${colorPlaying}${activePlayer.options.id}`);
        registeredPlayers.forEach(player => {
            if (player !== activePlayer) {
                pausePlayer(player);
            }
        });
    }

    return {
        initPlayer: function(wrapperId) {
            const playerId = `${wrapperId}-player`;
            document.querySelector(`#${wrapperId} > iframe:first-of-type`).id = playerId;

            const player = new PlayerSdk(`#${playerId}`, { id: playerId });
            player.addEventListener("play", () => pauseOtherPlayers(player));

            registeredPlayers.push(player);

            console.debug(`Video player initialized: ${colorNew}${playerId}`);
        },

        destroyAllPlayers: function() {
            registeredPlayers.forEach(player => {
                player.pause();
                player.destroy();
                console.debug(`Video player destroyed: ${colorDestroyed}${player.options.id}`);
            });
            registeredPlayers.length = 0;
        },

        logRegisteredPlayers: async function() {
            const playerIds = [];
            for (let player of registeredPlayers) {
                const id = player.options.id;
                await player.getPlaying() ?
                    playerIds.push(`${colorPlaying}${id} (Playing)`) :
                    playerIds.push(`${colorPaused}${id}`);
            }
            console.info(`Registered video players:\n\t${playerIds.join("\n\t")}`);
        }
    }
})();