import Scenario from './Scenario';

export default class Feature {
    constructor(feature) {
        this.id = feature.id;
        this.name = feature.name;
        this.description = feature.description;
        this.scenarios = feature.elements.map(element => new Scenario({
            id: element.id,
            type: element.type,
            name: element.name,
        }));
    }
}
