import React, {Component} from 'react';
import {StyleSheet,View,Pressable,Text} from 'react-native';

class OtherView extends Component{
    constructor(props){
        super(props);
        this.state = {
            thisData : this.props.setOtherView,
            position : this.props.position
        };
    };
    onPressInListener = (e) => {
        console.log(Date.now()," : ","onPressInListener of OtherView");
        this.props.onPressInListener(e);
    }
    onPressOutListener = (e) => {
        console.log(Date.now()," : ","onPressOutListener of OtherView");
        this.props.onPressOutListener(e);
    }
    render() {
        console.log(Date.now()," : ","OtherView Component");
        if(this.state.position=="coordinate") {
            return (
                <View key={this.state.thisData.ID} style={{position: "absolute",left: this.state.thisData.X, top: this.state.thisData.Y,height: this.state.thisData.Height, width: this.state.thisData.Width, alignContent: 'center', alignItems: "center",backgroundColor: 'black',borderBottomWidth: StyleSheet.hairlineWidth}}>
                    <Pressable style={{ height: this.state.thisData.Height, width: this.state.thisData.Width, alignContent: 'center',   justifyContent: 'center', alignItems: "center", backgroundColor: 'red'}}
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
        else if(this.state.position=="automatic") {
            return (
                <View key={this.state.thisData.ID} style={{height: this.state.thisData.Height, width: this.state.thisData.Width, alignContent: 'center', alignItems: "center",backgroundColor: 'black',borderBottomWidth: StyleSheet.hairlineWidth}}>
                    <Pressable style={{height: this.state.thisData.Height, width: this.state.thisData.Width, alignContent: 'center',   justifyContent: 'center', alignItems: "center", backgroundColor: 'red'}}
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

export default OtherView
