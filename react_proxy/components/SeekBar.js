import React, {Component} from 'react';
import {StyleSheet,View,Pressable,Text,TouchableOpacity,ImageBackground} from 'react-native';
import Slider  from '@react-native-community/slider';
var previousPressIn;
var touchMode;
class SeekBarView extends Component{
    constructor(props){
        super(props);
        this.state = {
            thisData : this.props.setSeekBar,
            position : this.props.position
        };
    };
    onPressInListener = (e) => {
        console.log(e.nativeEvent.locationX);
        console.log(e.nativeEvent.locationY);
        console.log(Date.now()," : ","onPressInListener of SeekBar");
        previousPressIn = e;
        touchMode = 0;
        // this.props.onPressInListener(e);
    }
    onLongPressListener = (e) => {
        console.log(Date.now()," : ","onLongPressListener of SeekBar");
        touchMode = 1;
    }
    onSlideListener = (e) => {
        console.log("onSlideListener of SeekBar : ",e);
        this.props.onSlideListener(this.state.thisData.ID,e);
    }
    onPressOutListener = (e) => {
        console.log(Date.now()," : ","onPressOutListener of SeekBar");

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
        console.log(Date.now()," : ","Slider Component created");
        console.log("FLUID(EXP) react: SeekBar Component created : ",global.nativePerformanceNow());
        var image = this.state.thisData.Image;
        if(this.state.position=="coordinate") {
            return (
                <View key={this.state.thisData.ID} style={{position: "absolute",left: this.state.thisData.X, top: this.state.thisData.Y,height: this.state.thisData.Height, width: this.state.thisData.Width, alignContent: 'center', alignItems: "center",backgroundColor: 'black',borderBottomWidth: StyleSheet.hairlineWidth}}>
                    <TouchableOpacity style={{ height: this.state.thisData.Height, width: this.state.thisData.Width, alignContent: 'center',   justifyContent: 'center', alignItems: "center", backgroundColor: 'skyblue'}}
                    id={this.state.thisData.ID}
                    onPressIn={this.onPressInListener}
                    onLongPress={this.onLongPressListener}
                    onPressOut={this.onPressOutListener}>
                        <Slider style={{ height: this.state.thisData.Height, width: this.state.thisData.Width, alignContent: 'center',   justifyContent: 'center', alignItems: "center", backgroundColor: 'skyblue'}}
                                id={this.state.thisData.ID}
                                maximumValue={this.state.thisData.Max}
                                value={this.state.thisData.Progress}
                                onValueChange={this.onSlideListener}
                                />
                    </TouchableOpacity>
                
                    
                </View>
            )
        }
        else if(this.state.position=="automatic") {
            return (
                <View key={this.state.thisData.ID} style={{height: this.state.thisData.Height, width: this.state.thisData.Width, alignContent: 'center', alignItems: "center",backgroundColor: 'black',borderBottomWidth: StyleSheet.hairlineWidth}}>
                    

                    <TouchableOpacity style={{flexDirection: 'row', height: this.state.thisData.Height, width: this.state.thisData.Width, alignContent: 'center',   justifyContent: 'center', alignItems: "center", backgroundColor: 'skyblue'}}
                    id={this.state.thisData.ID}
                    onPressIn={this.onPressInListener}
                    onLongPress={this.onLongPressListener}
                    onPressOut={this.onPressOutListener}>
                        <Text>{this.state.thisData.Text}</Text>
                        <Slider style={{ height: this.state.thisData.Height, width: this.state.thisData.Width, alignContent: 'center',   justifyContent: 'center', alignItems: "center", backgroundColor: 'skyblue'}}
                                id={this.state.thisData.ID}
                                maximumValue={this.state.thisData.Max}
                                value={this.state.thisData.Progress}
                                onValueChange={this.onSlideListener}
                                />
                    </TouchableOpacity>

                </View>
            )
        }
        
    };
}

export default SeekBarView;
