declare class PlayerOptions {
    id: string;
}

declare class PlayerSdk extends HTMLElement {
  constructor(playerId: string, options: PlayerOptions);
  options: PlayerOptions;
  destroy(): void;
  getPlaying(): boolean;
  getPlaying(fn: (isPlaying: boolean) => void): void;
  pause(): void;
}

export = PlayerSdk;
