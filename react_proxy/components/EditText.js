import React, {Component} from 'react';
import {StyleSheet,View, TextInput,Pressable} from 'react-native';

var previousPressIn;
var touchMode;
class EditText extends Component{
    constructor(props){
        super(props);
        this.state = {
            thisData : this.props.setEditText,
            position : this.props.position
        };
        console.log(Date.now()," : ",props);
    };
    onPressInListener = (e) => {
        console.log(e.nativeEvent.locationX);
        console.log(e.nativeEvent.locationY);
        console.log(Date.now()," : ","onPressInListener of ImageView");
        previousPressIn = e;
        touchMode = 0;
        // this.props.onPressInListener(e);
    }
    onLongPressListener = (e) => {
        console.log(Date.now()," : ","onLongPressListener of ImageView");
        touchMode = 1;
    }

    onPressOutListener = (e) => {
        console.log(Date.now()," : ","onPressOutListener of ImageView");

        console.log("out: ",e.nativeEvent.locationX);
        console.log("out: ",e.nativeEvent.locationY);
        if(touchMode == 0)
        {
            this.props.onPressInListener(e);
            this.props.onPressOutListener(e);
        }
        else if(touchMode == 1)
        {
            this.props.setLayoutTouchMode(1,e.target._internalFiberInstanceHandleDEV.memoizedProps.id);
        }
        
    }

    TextChangeListener = (e) => {
        console.log(e);
        this.props.TextChangeListener(e);
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
                        onLongPress={this.onLongPressListener}
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
                        onLongPress={this.onLongPressListener}
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
