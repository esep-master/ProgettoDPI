import { ITipoBeacon } from "./ITipoBeacon";

export interface IBeacon {
    id: number,
    seriale: string,
    livelloBatteria: number,
    tipoBeacon: ITipoBeacon
}