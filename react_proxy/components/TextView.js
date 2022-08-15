import React, {useState, Component} from 'react';
import {StyleSheet, View, Text} from 'react-native';

class TextView extends Component{
    constructor(props){
        super(props);
        this.state = {
            thisData : this.props.setTextView
        };
    };
    TextChangeListener = (e) => {
        this.props.TextChangeListener(e);
        console.log("this is dummy TextChangeListener of TextView");
    }
    render() {
        
        return (
            <View key={this.state.thisData.ID} style={{alignItems:'flex-start'}}>
                <Text  style={{fontSize: this.state.thisData.TextSize, textAlign: 'left', fontWeight: '500', width: 350}}>
                    {this.state.thisData.Text}
                </Text>
            </View>
        )
    };
}

export default TextView

