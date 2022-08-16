import React, {Component} from 'react';
import {StyleSheet,View, TextInput} from 'react-native';

class EditText extends Component{
    constructor(props){
        super(props);
        this.state = {
            thisData : this.props.setEditText,
            position : this.props.position
        };
    };
    TextChangeListener = (e) => {
        this.props.TextChangeListener(e);
        console.log("this is dummy TextChangeListener of EditText");
    }
    render() {
        
        return (
            <View key={this.state.thisData.ID} style={{position: this.state.position, left: this.state.thisData.X, top: this.state.thisData.Y, alignItems:'flex-start', borderBottonWidth : StyleSheet.hairlineWidth}}>
                <TextInput style={{fontSize: this.state.thisData.TextSize,  textAlign: 'left', padding: 2, color: this.state.thisData.Color}} 
                    value={this.state.thisData.Text}
                    id={this.state.thisData.ID}
                    onChange={this.TextChangeListener}>
                </TextInput>
            </View>
        )
    };
}

export default EditText