import React, {Component} from 'react';
import {StyleSheet,View,Pressable,Text} from 'react-native';

class Button extends Component{
    constructor(props){
        super(props);
        this.state = {
            thisData : this.props.setButton,
            position : this.props.position
        };
    };
    onPressInListener = (e) => {
        console.log("onPressInListener of Button");
        this.props.onPressInListener(e);
    }
    onPressOutListener = (e) => {
        console.log("onPressOutListener of Button");
        this.props.onPressOutListener(e);
    }
    render() {
        console.log("Button",this.state.thisData);
        if(this.state.position==="cordinate") {
            return (
                <View key={this.state.thisData.ID} style={{position: "absolute",left: this.state.thisData.X, top: this.state.thisData.Y,height: this.state.thisData.Height, width: this.state.thisData.Width, alignContent: 'center', alignItems: "center",backgroundColor: 'black',borderBottomWidth: StyleSheet.hairlineWidth}}>
                    <Pressable style={{ height: this.state.thisData.Height, width: this.state.thisData.Width, alignContent: 'center',   justifyContent: 'center', alignItems: "center", backgroundColor: 'skyblue'}}
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
        }
        else if(this.state.position==="automatic") {
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
        }
        
    };
}

export default Button