import React, {Component} from 'react';
import {StyleSheet,View,Pressable,Text} from 'react-native';

class Button extends Component{
    constructor(props){
        super(props);
        this.state = {
            thisData : this.props.setButton
        };
    };
    onPressInListener = (e) => {
        this.props.onPressInListener(e);
    }
    onPressOutListener = (e) => {
        this.props.onPressOutListener(e);
    }
    render() {
        
        return (
            <View key={this.state.thisData.ID} style={{height: this.state.thisData.Height, width: this.state.thisData.Width, alignContent: 'center', alignItems: "center",backgroundColor: 'black',borderBottomWidth: StyleSheet.hairlineWidth}}>
                <Pressable style={{height: this.state.thisData.Height, width: this.state.thisData.Width, alignContent: 'center',   justifyContent: 'center', alignItems: "center", backgroundColor: 'skyblue'}}
                    id={this.state.thisData.ID}
                    onPressIn={this.onPressInListener}
                    onPressOut={this.onPressOutListener}>
                    <Text 
                        style={{fontSize: 30, textAlign: 'center', alignContent: 'center', color: 'black'}}
                        id={this.state.thisData.ID}> 
                            {this.state.thisData.Text} 
                    </Text>
                    
                </Pressable>
            </View>
        )
    };
}

export default Button