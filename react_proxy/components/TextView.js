import React, {useState, Component} from 'react';
import {StyleSheet, View, Text, Pressable, ImageBackground} from 'react-native';

var previousPressIn;
var touchMode;
class TextView extends Component{
    constructor(props){
        super(props);
        this.state = {
            thisData : this.props.setTextView,
            position : this.props.position
        };
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
    render() {
        console.log(Date.now()," : ","TextView Component created",this.state.thisData.ID);
        var image = this.state.thisData.Image;
        if(this.state.position=="coordinate")
        {
            
            return (
                <View key={this.state.thisData.ID} style={{position: "absolute",left: this.state.thisData.X, top: this.state.thisData.Y, left: this.state.thisData.X, top: this.state.thisData.Y, alignItems:'flex-start'}}>
                    <Pressable style={{alignContent: 'center',   justifyContent: 'center', alignItems: "center", height: this.state.thisData.Height, width: this.state.thisData.Width,}}
                        id={this.state.thisData.ID}
                        onPressIn={this.onPressInListener}
                        onLongPress={this.onLongPressListener}
                        onPressOut={this.onPressOutListener}>

                            <ImageBackground
                                id={this.state.thisData.ID}
                                source={{ uri: `data:image/png;base64,${image}`, }} 
                                style={{alignItems:'center', height: this.state.thisData.Height, width: this.state.thisData.Width}} resizeMode={'contain'}>
                                    <Text 
                                        style={{fontSize: this.state.thisData.TextSize, textAlign: 'left', alignContent: 'center', color: this.state.thisData.Color,fontWeight: '500'}}
                                        id={this.state.thisData.ID}> 
                                            {this.state.thisData.Text} 
                                    </Text>
                            </ImageBackground>
                        
                    </Pressable>
                    
                    {/* <Text  style={{fontSize: this.state.thisData.TextSize, textAlign: 'left', fontWeight: '500', width: 350}}>
                        {this.state.thisData.Text}
                    </Text> */}
                </View>
            )
        }
        else if(this.state.position=="automatic")
        {
            return (
                <View key={this.state.thisData.ID} style={{alignItems:'flex-start'}}>
                    <Pressable style={{alignContent: 'center',   justifyContent: 'center', alignItems: "center", }}
                        id={this.state.thisData.ID}
                        onPressIn={this.onPressInListener}
                        onLongPress={this.onLongPressListener}
                        onPressOut={this.onPressOutListener}>
                            <ImageBackground
                                id={this.state.thisData.ID}
                                source={{ uri: `data:image/png;base64,${image}`, }} 
                                style={{alignItems:'center', height: this.state.thisData.Height, width: this.state.thisData.Width}} resizeMode={'contain'}>
                                    <Text  
                                        style={{fontSize: this.state.thisData.TextSize, textAlign: 'left', fontWeight: '500', width: 350}}
                                        id={this.state.thisData.ID}>
                                            {this.state.thisData.Text}
                                    </Text>
                            </ImageBackground>
                    </Pressable>
                </View>
            )
        }
    };
}

export default TextView

