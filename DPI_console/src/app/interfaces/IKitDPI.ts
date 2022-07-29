import { IDPI } from "./IDPI";
import { IKit } from "./IKit";

export interface IKitDPI {
    id: number,
    dpi: IDPI,
    kit?: IKit,
    sbloccoAllarmeDa?: string,
    sbloccoAllarmeA?: string
}