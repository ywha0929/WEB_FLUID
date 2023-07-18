import React, {Component} from 'react';
import {StyleSheet, Pressable, View, Text, TextInput} from 'react-native';
import Button from './Button';
import EditText from './EditText';
import TextView from './TextView';
import ImageView from './ImageView';
import OtherView from './OtherView';
import SwitchView from './Switch';
import SeekBarView from './SeekBar';
import RadioGroupView from './RadioGroup';
var layoutTouchMode;
var childMoveId;
class OtherLayout extends Component{
    constructor(props) {
        super(props);
        this.state = {
            thisData: this.props.setOtherLayout,
            UIList: this.props.UIList
            
        };

        layoutTouchMode = 0;
        // console.log(Date.now()," : ","new OtherLayout instance created",props);
    }
    TextChangeListener=(e)=>{
        this.props.TextChangeListener(e);
        console.log(Date.now()," : ","this is dummy TextChangeListener of LinearLayout");
    }

    onPressInListener=(e)=>{
        // this.props.onPressInListener(e);
        if(layoutTouchMode == 1)
        {
            this.props.moveComponent(childMoveId,e);
            layoutTouchMode = 0;
        }
    }
    onPressOutListener=(e)=>{
        this.props.onPressOutListener(e);
        console.log(Date.now()," : ","this is dummy onPressOutListener of LinearLayout");
    }
    LayoutPressInListener=(e)=>{
        console.log(Date.now()," : ","this is dummy onPressOutListener of LinearLayout");
    }
    setLayoutTouchMode=(mode,id)=>{
        layoutTouchMode=mode;
        childMoveId=id;
        this.props.rerender();
    }
    render() {
        let UIs = this.state.UIList.map((item,index)=>{
            if(item.Parent_ID == this.state.thisData.ID){
                if(item.WidgetType.includes("EditText")){
                    console.log(Date.now()," : ","OtherLayout : passing to EditText");
                    return (
                        <EditText 
                            key={item.ID}
                            setEditText={item}
                            position={"coordinate"}
                            setLayoutTouchMode={this.setLayoutTouchMode}
                            TextChangeListener={this.TextChangeListener}
                            onPressInListener={this.props.onPressInListener}
                            onPressOutListener={this.props.onPressOutListener}
                            />
                    );
                }
                if(item.WidgetType.includes("TextView")){
                    console.log(Date.now()," : ","OtherLayout : passing to TextView");
                    return (
                        <TextView 
                            key={item.ID}
                            setTextView={item}
                            position={"coordinate"}
                            setLayoutTouchMode={this.setLayoutTouchMode}
                            TextChangeListener={this.props.TextChangeListener}
                            onPressInListener={this.props.onPressInListener}
                            onPressOutListener={this.props.onPressOutListener}/>
                        
                    );
                }
                if(item.WidgetType.includes("Button")){
                    console.log(Date.now()," : ","OtherLayout : passing to Button");
                    return(
                        <Button 
                            key={item.ID}
                            setButton={item} 
                            position={"coordinate"}
                            setLayoutTouchMode={this.setLayoutTouchMode}
                            onPressInListener={this.props.onPressInListener}
                            onPressOutListener={this.props.onPressOutListener}/>
                    )
                }
                if(item.WidgetType.includes("ImageView")) {
                    console.log(Date.now()," : ","OtherLayout : passing to ImageView");
                    return (
                        <ImageView
                            key={item.ID}
                            setImageView={item}
                            position={"coordinate"}
                            setLayoutTouchMode={this.setLayoutTouchMode}
                            onPressInListener={this.props.onPressInListener}
                            onPressOutListener={this.props.onPressOutListener}/>
                    )
                }
                if(item.WidgetType.includes("SeekBar")) {
                    console.log(Date.now()," : ","LinearLayout : SeekBar");
                    return (
                        <SeekBarView
                            key={item.ID}
                            setSeekBar={item}
                            position={"coordinate"}
                            onSlideListener={this.onSlideListener}
                            setLayoutTouchMode={this.props.setLayoutTouchMode}
                            onPressInListener={this.props.onPressInListener}
                            onPressOutListener={this.props.onPressOutListener}/>
                    )
                }
                if(item.WidgetType.includes("Switch")) {
                    console.log(Date.now()," : ","LinearLayout : Switch");
                    return (
                        <SwitchView
                        key={item.ID}
                        setSwitch={item}
                        position={"coordinate"}
                        
                        setLayoutTouchMode={this.setLayoutTouchMode}
                        onPressInListener={this.props.onPressInListener}
                        onPressOutListener={this.props.onPressOutListener}
                        onToggleListener={this.props.onToggleListener}/>
                    )
                }
                if(item.WidgetType.includes("RadioGroup")) {
                    console.log(Date.now()," : ","LinearLayout : Switch");
                    return (
                        <RadioGroupView
                        key={item.ID}
                        setRadioGroup={item}
                        position={"coordinate"}
                        onItemClickListener={this.props.onItemClickListener}
                        setLayoutTouchMode={this.setLayoutTouchMode}
                        onPressInListener={this.props.onPressInListener}
                        onPressOutListener={this.props.onPressOutListener}
                        onToggleListener={this.props.onToggleListener}/>
                    )
                }
                if(item.WidgetType.includes("OtherView")) {
                    console.log(Date.now()," : ","OtherLayout : passing to OtherView");
                    return (
                        <OtherView 
                            key={item.ID}
                            setOtherView={item}
                            position={"coordinate"}
                            setLayoutTouchMode={this.setLayoutTouchMode}
                            onPressInListener={this.props.onPressInListener}
                            onPressOutListener={this.props.onPressOutListener}/>
                    )
                }
            }
            else{
                // console.log(Date.now()," : ",'OtherLayout : not child');
            }
            
        });


        //console.log(Date.now()," : ","OtherLayout : ", this.state.thisData);
        if(layoutTouchMode== 0)
        {
            return(
                <View 
                    key={this.state.thisData.ID}
                    style={{height: this.state.thisData.Height+ 10, width: this.state.thisData.Width + 10,orderBottomWidth: StyleSheet.hairlineWidth}}>
                    {UIs}

                </View>
            )
        }
        else {
            return(
                <View 
                    key={this.state.thisData.ID}
                    style={{height: this.state.thisData.Height+ 10, width: this.state.thisData.Width + 10, backgroundColor: 'blanchedalmond',orderBottomWidth: StyleSheet.hairlineWidth}}>
                    <Pressable style={{height: this.state.thisData.Height, width: this.state.thisData.Width, backgroundColor: 'lightblue',orderBottomWidth: StyleSheet.hairlineWidth}}
                        onPressIn={this.onPressInListener}>
                    {UIs}
                    </Pressable>
                </View>
            )
        }
        
        
        
        
    }
}

const styles = StyleSheet.create({
    LinearLayout:{

    }
})

export default OtherLayout
