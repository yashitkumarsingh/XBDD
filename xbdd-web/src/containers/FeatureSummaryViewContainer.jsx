import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core/styles';
import { Card } from '@material-ui/core';
import Feature from '../models/Feature';
import FeatureHistory from '../models/FeatureHistory';
import FeatureBuildHistoryViewContainer from '../components/FeatureBuildHistoryView';
import FeatureSummaryView from '../components/FeatureSummaryView';

const styles = theme => ({
    featureSummaryViewContainer: {
        width: '100%',
        maxWidth: 770,
        backgroundColor: theme.palette.background.paper,
    },
    featureSummaryViewLeftColumn: {
        float: 'left',
        width: '25%',
        paddingLeft: '15px',
        paddingRight: '15px',
        boxSizing: 'border-box',
    },
    featureSummaryViewRightColumn: {
        float: 'left',
        width: '75%',
        paddingLeft: '15px',
        paddingRight: '15px',
        boxSizing: 'border-box',
    },
});

class FeatureSummaryViewContainer extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
    }

    render() {
        const { classes } = this.props;
        return (
            <Card raised className={classes.featureSummaryViewContainer}>
                <div className={classes.featureSummaryViewLeftColumn}>
                    <i>placeholder for edit status and tags</i>
                </div>
                <div className={classes.featureSummaryViewRightColumn}>
                    <FeatureSummaryView feature={this.props.feature} />
                    <FeatureBuildHistoryViewContainer
                        featureRollupData={this.props.featureRollupData}
                    />
                </div>
            </Card>
        );
    }
}

FeatureSummaryViewContainer.propTypes = {
    classes: PropTypes.object.isRequired, // eslint-disable-line react/forbid-prop-types
    feature: PropTypes.instanceOf(Feature).isRequired,
    featureRollupData: PropTypes.instanceOf(FeatureHistory).isRequired,
};

export default withStyles(styles)(FeatureSummaryViewContainer);
