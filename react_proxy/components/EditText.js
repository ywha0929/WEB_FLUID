import React, {Component} from 'react';
import {StyleSheet,View, TextInput,Pressable} from 'react-native';

class EditText extends Component{
    constructor(props){
        super(props);
        this.state = {
            thisData : this.props.setEditText,
            position : this.props.position
        };
        console.log(props);
    };
    TextChangeListener = (e) => {
        console.log("this is dummy TextChangeListener of EditText");
        this.props.TextChangeListener(e);
        
    }
    onPressInListener = (e) => {
        console.log("onPressInListener of EditText");
        this.props.onPressInListener(e);
    }
    onPressOutListener = (e) => {
        console.log("onPressOutListener of EditText");
        this.props.onPressOutListener(e);
    }
    render() {
        console.log("EditText Component");
        if(this.state.position==="cordinate")
        {
            return (
                <View key={this.state.thisData.ID} style={{position: "absolute",left: this.state.thisData.X, top: this.state.thisData.Y, alignItems:'flex-start', borderBottonWidth : StyleSheet.hairlineWidth}}>
                    <Pressable style={{alignContent: 'center',   justifyContent: 'center', alignItems: "center", }}
                        id={this.state.thisData.ID}
                        onPressIn={this.onPressInListener}
                        onPressOut={this.onPressOutListener}>
                    
                        <TextInput style={{fontSize: this.state.thisData.TextSize,  textAlign: 'left', padding: 2, color: this.state.thisData.Color}} 
                            value={this.state.thisData.Text}
                            id={this.state.thisData.ID}
                            onChange={this.TextChangeListener}>
                        </TextInput>
                    </Pressable>
                </View>
            )
        }
        else if(this.state.position==="automatic")
        {
            return (
                <View key={this.state.thisData.ID} style={{alignItems:'flex-start', borderBottonWidth : StyleSheet.hairlineWidth}}>
                    <Pressable style={{alignContent: 'center',   justifyContent: 'center', alignItems: "center", }}
                        id={this.state.thisData.ID}
                        onPressIn={this.onPressInListener}
                        onPressOut={this.onPressOutListener}>
                        <TextInput style={{fontSize: this.state.thisData.TextSize,  textAlign: 'left', padding: 2, color: this.state.thisData.Color}} 
                            value={this.state.thisData.Text}
                            id={this.state.thisData.ID}
                            onChange={this.TextChangeListener}>
                        </TextInput>
                    </Pressable>
                </View>
            )
        }
    };
}

export default EditText