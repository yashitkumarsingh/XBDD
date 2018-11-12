import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core/styles';
import Feature from '../models/Feature';

const styles = theme => ({
    featureSummaryView: {
        width: '100%',
        maxWidth: 540,
        backgroundColor: theme.palette.background.paper,
    },
    featureSummaryViewTitle: {
        textAlign: 'center',
        fontWeight: 'bold',
    },
    featureSummaryViewDescription: {
        textAlign: 'left',
        whiteSpace: 'pre-wrap',
        fontSize: '85%',
    },
});

class FeatureSummaryView extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
    }

    render() {
        const { classes } = this.props;
        return (
            <div className={classes.featureSummaryView}>
                <div className={classes.featureSummaryViewTitle}>{this.props.feature.name}</div>
                <p className={classes.featureSummaryViewDescription}>
                    {this.props.feature.description}
                </p>
            </div>
        );
    }
}

FeatureSummaryView.propTypes = {
    classes: PropTypes.object.isRequired, // eslint-disable-line react/forbid-prop-types
    feature: PropTypes.instanceOf(Feature).isRequired,
};

export default withStyles(styles)(FeatureSummaryView);
