import React, {useState, Component} from 'react';
import {StyleSheet, View, Text, Pressable} from 'react-native';

class TextView extends Component{
    constructor(props){
        super(props);
        this.state = {
            thisData : this.props.setTextView,
            position : this.props.position
        };
    };
    TextChangeListener = (e) => {
        this.props.TextChangeListener(e);
        console.log("this is dummy TextChangeListener of TextView");
    }

    onPressInListener = (e) => {
        console.log("onPressInListener of TextView");
        this.props.onPressInListener(e);
    }
    onPressOutListener = (e) => {
        console.log("onPressOutListener of TextView");
        this.props.onPressOutListener(e);
    }
    render() {
        console.log("TextView Component");
        if(this.state.position==="cordinate")
        {
            return (
                <View key={this.state.thisData.ID} style={{position: "absolute",left: this.state.thisData.X, top: this.state.thisData.Y, left: this.state.thisData.X, top: this.state.thisData.Y, alignItems:'flex-start'}}>
                    <Pressable style={{alignContent: 'center',   justifyContent: 'center', alignItems: "center", height: this.state.thisData.Height, width: this.state.thisData.Width,}}
                        id={this.state.thisData.ID}
                        onPressIn={this.onPressInListener}
                        onPressOut={this.onPressOutListener}>
                        <Text 
                            style={{fontSize: this.state.thisData.TextSize, textAlign: 'left', alignContent: 'center', color: 'black',fontWeight: '500'}}
                            id={this.state.thisData.ID}> 
                                {this.state.thisData.Text} 
                        </Text>
                        
                    </Pressable>
                    
                    {/* <Text  style={{fontSize: this.state.thisData.TextSize, textAlign: 'left', fontWeight: '500', width: 350}}>
                        {this.state.thisData.Text}
                    </Text> */}
                </View>
            )
        }
        else if(this.state.position==="automatic")
        {
            return (
                <View key={this.state.thisData.ID} style={{alignItems:'flex-start'}}>
                    <Pressable style={{alignContent: 'center',   justifyContent: 'center', alignItems: "center", }}
                        id={this.state.thisData.ID}
                        onPressIn={this.onPressInListener}
                        onPressOut={this.onPressOutListener}>
                        <Text  
                            style={{fontSize: this.state.thisData.TextSize, textAlign: 'left', fontWeight: '500', width: 350}}
                            id={this.state.thisData.ID}>
                                {this.state.thisData.Text}
                        </Text>
                    </Pressable>
                </View>
            )
        }
    };
}

export default TextView

