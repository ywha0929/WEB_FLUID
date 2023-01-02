import React, {Component} from 'react';
import {StyleSheet,View, TextInput,Pressable} from 'react-native';

class EditText extends Component{
    constructor(props){
        super(props);
        this.state = {
            thisData : this.props.setEditText,
            position : this.props.position
        };
        console.log(Date.now()," : ",props);
    };
    TextChangeListener = (e) => {
        console.log(Date.now()," : ","this is dummy TextChangeListener of EditText");
        this.props.TextChangeListener(e);
        
    }
    onPressInListener = (e) => {
        console.log(Date.now()," : ","onPressInListener of EditText");
        this.props.onPressInListener(e);
    }
    onPressOutListener = (e) => {
        console.log(Date.now()," : ","onPressOutListener of EditText");
        this.props.onPressOutListener(e);
    }
    render() {
        console.log(Date.now()," : ","EditText Component created");
        if(this.state.position=="coordinate")
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
        else if(this.state.position=="automatic")
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
